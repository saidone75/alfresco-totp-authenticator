/*
 * Alfresco TOTP authenticator - two factor authentication for Alfresco
 * Copyright (C) 2021-2022 Saidone
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.saidone.alfresco.repo.security.authentication;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Slf4j
public final class TotpService {

    @Setter
    private static PersonService personService;
    @Setter
    private static NodeService nodeService;
    @Setter
    private static String issuer;

    public static final QName totpSecretQname = QName.createQName("org.saidone", "totpsecret");

    private TotpService() {}

    public static void authorizeToken(String username, String token) {
        try {
            AuthenticationUtil.runAs(
                    (AuthenticationUtil.RunAsWork<String>) () -> {
                        String secret = (String) nodeService.getProperty(
                                personService.getPerson(username),
                                totpSecretQname);
                        if (secret != null &&
                                !new DefaultCodeVerifier(
                                        new DefaultCodeGenerator(),
                                        new SystemTimeProvider())
                                        .isValidCode(secret, token)) {
                            throw new AuthenticationException("Invalid token");
                        }
                        return null;
                    }, AuthenticationUtil.getSystemUserName());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public static void generateSecret(String user) {
        nodeService.setProperty(
                personService.getPerson(user),
                totpSecretQname,
                new DefaultSecretGenerator().generate());
    }

    public static void setSecret(String user, String secret) {
        NodeRef userNodeRef = personService.getPerson(user);
        if ("".equals(secret)) {
            nodeService.removeProperty(userNodeRef, totpSecretQname);
        } else {
            nodeService.setProperty(userNodeRef, totpSecretQname, secret);
        }
    }

    public static String getSecret(String user) {
        return (String) nodeService.getProperty(
                personService.getPerson(user),
                totpSecretQname);
    }

    public static String getDataUri(String user) {
        String secret = getSecret(user);
        String dataUri;
        if (null == secret) {
            dataUri = null;
        } else {
            QrData data = new QrData.Builder()
                    .label(user)
                    .secret(secret)
                    .issuer(issuer)
                    .algorithm(HashingAlgorithm.SHA1)
                    .digits(6)
                    .period(30)
                    .build();
            QrGenerator generator = new ZxingPngQrGenerator();
            byte[] imageData = null;
            try {
                imageData = generator.generate(data);
            } catch (QrGenerationException e) {
                log.error(e.getMessage());
                if (log.isTraceEnabled()) e.printStackTrace();
            }
            dataUri = getDataUriForImage(imageData, generator.getImageMimeType());
        }
        return dataUri;
    }

    public static void init() {
        log.info("Starting " + TotpService.class.getName());
    }
}
