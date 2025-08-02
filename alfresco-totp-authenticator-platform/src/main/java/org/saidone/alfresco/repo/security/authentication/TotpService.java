/*
 * Alfresco TOTP authenticator - two factor authentication for Alfresco
 * Copyright (C) 2021-2025 Saidone
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import lombok.val;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Slf4j
/**
 * Utility service providing TOTP generation, validation and QR code helpers for
 * Alfresco users.
 */
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

    /**
     * QName of the property storing the TOTP secret on the person node.
     */
    public static final QName totpSecretQname = QName.createQName("org.saidone", "totpsecret");

    private TotpService() {}

    /**
     * Validates the provided token for the given user. If the token does not
     * match the stored secret an {@link AuthenticationException} is thrown.
     *
     * @param username user name
     * @param token    one-time TOTP token
     */
    public static void authorizeToken(String username, String token) {
        try {
            AuthenticationUtil.runAs(
                    (AuthenticationUtil.RunAsWork<String>) () -> {
                        val secret = (String) nodeService.getProperty(
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

    /**
     * Generates and stores a new TOTP secret for the given user.
     *
     * @param user user name
     */
    public static void generateSecret(String user) {
        nodeService.setProperty(
                personService.getPerson(user),
                totpSecretQname,
                new DefaultSecretGenerator().generate());
    }

    /**
     * Sets the provided TOTP secret for the user or removes it if blank.
     *
     * @param user   user name
     * @param secret new secret value
     */
    public static void setSecret(String user, String secret) {
        val userNodeRef = personService.getPerson(user);
        if (Strings.isBlank(secret)) {
            nodeService.removeProperty(userNodeRef, totpSecretQname);
        } else {
            nodeService.setProperty(userNodeRef, totpSecretQname, secret);
        }
    }

    /**
     * Retrieves the stored TOTP secret for the user.
     *
     * @param user user name
     * @return stored secret or {@code null} if none exists
     */
    public static String getSecret(String user) {
        return (String) nodeService.getProperty(
                personService.getPerson(user),
                totpSecretQname);
    }

    /**
     * Builds a data URI representing a QR code for the user's secret suitable for
     * consumption by authenticator applications.
     *
     * @param user user name
     * @return data URI string or {@code null} if no secret exists
     */
    public static String getDataUri(String user) {
        val secret = getSecret(user);
        String dataUri;
        if (null == secret) {
            dataUri = null;
        } else {
            val data = new QrData.Builder()
                    .label(user)
                    .secret(secret)
                    .issuer(issuer)
                    .algorithm(HashingAlgorithm.valueOf(algorithm))
                    .digits(digits)
                    .period(period)
                    .build();
            val generator = new ZxingPngQrGenerator();
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

    /**
     * Logs the start of the service. Intended to be called by the Spring
     * container during initialization.
     */
    public static void init() {
        log.info("Starting {}", TotpService.class.getName());
    }

}
