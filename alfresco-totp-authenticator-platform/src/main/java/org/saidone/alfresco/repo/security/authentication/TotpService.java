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

import com.rometools.utils.Strings;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
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
    private static MessageService messageService;
    @Setter
    private static String issuer;
    @Setter
    private static String algorithm;
    @Setter
    private static int digits;
    @Setter
    private static int period;

    public static final QName totpSecretQname = QName.createQName("org.saidone", "totpsecret");

    private TotpService() {}

    public static void authorizeToken(String username, String token) {
        try {
            AuthenticationUtil.runAs(
                    (AuthenticationUtil.RunAsWork<String>) () -> {
                        var secret = (String) nodeService.getProperty(
                                personService.getPerson(username),
                                totpSecretQname);
                        if (secret != null &&
                                !new DefaultCodeVerifier(
                                        new DefaultCodeGenerator(),
                                        new SystemTimeProvider())
                                        .isValidCode(secret, token)) {
                            throw new AuthenticationException(messageService.getMessage("totpauthenticator.invalid_token"));
                        }
                        return null;
                    }, AuthenticationUtil.getAdminUserName());
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
        var userNodeRef = personService.getPerson(user);
        if (Strings.isBlank(secret)) {
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
        var secret = getSecret(user);
        String dataUri;
        if (null == secret) {
            dataUri = null;
        } else {
            var data = new QrData.Builder()
                    .label(user)
                    .secret(secret)
                    .issuer(issuer)
                    .algorithm(HashingAlgorithm.valueOf(algorithm))
                    .digits(digits)
                    .period(period)
                    .build();
            var generator = new ZxingPngQrGenerator();
            byte[] imageData = null;
            try {
                imageData = generator.generate(data);
            } catch (QrGenerationException e) {
                log.error(e.getMessage());
                log.trace(e.getMessage(), e);
            }
            dataUri = getDataUriForImage(imageData, generator.getImageMimeType());
        }
        return dataUri;
    }

    public static void init() {
        log.info("Starting {}", TotpService.class.getName());
    }

}
