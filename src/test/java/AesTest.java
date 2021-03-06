/*
 * _________  ____ ______________.___.____       _________
 * \_   ___ \|    |   \__    ___/|   |    |     /   _____/
 * /    \  \/|    |   / |    |   |   |    |     \_____  \
 * \     \___|    |  /  |    |   |   |    |___  /        \
 *  \______  /______/   |____|   |___|_______ \/_______  /
 *         \/                                \/        \/
 *
 * Copyright (C) 2018 — 2020 Honerfor, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.honerfor.cutils.security.AES;
import java.io.Serializable;
import java.util.stream.Stream;
import javax.crypto.AEADBadTagException;
import lombok.ToString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Test AES Encryption and Decryption operation.")
final class AesTest {

  @ToString
  private static class PersonExample implements Serializable {
    private static final long serialVersionUID = -4359123926347587815L;

    private int age;
    private String name;

    void setAge(int age) {
      this.age = age;
    }

    void setName(String name) {
      this.name = name;
    }
  }

  @DisplayName("Should successfully Encrypt and Decrypt Custom Object with Custom Encryption Key")
  @ParameterizedTest(name = "{index} => input={0}")
  @MethodSource("customObjectResource")
  void encryptionAndDecryptionCustomObjectWithCustomKey(final PersonExample input, final String key)
      throws Exception {
    final AES<PersonExample> aes = AES.init(key);
    final String encryptedPersonExample = aes.encrypt(input);
    System.out.println(encryptedPersonExample);
    final PersonExample decryptedPersonExample = aes.decrypt(encryptedPersonExample);

    assertEquals(decryptedPersonExample.age, input.age);
    assertEquals(decryptedPersonExample.name, input.name);
  }

  private static Stream<Arguments> customObjectResource() {
    final PersonExample personExampleI =
        new PersonExample() {
          {
            setAge(10);
            setName("B0B");
          }
        };

    final PersonExample personExampleII =
        new PersonExample() {
          {
            setAge(11);
            setName("PETER");
          }
        };

    return Stream.of(
        Arguments.of(personExampleI, "P37s0n3x4mpl3-Cust0m-k3y"),
        Arguments.of(personExampleII, "n3w P37s0n3x4mpl3-Cust0m-k3y"));
  }

  @DisplayName("Should Throw IllegalArgumentException when trying to encrypt Null values.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("illegalValuesResource")
  void throwIllegalArgumentExceptionOnEncryption(final Object value) {
    assertThrows(IllegalArgumentException.class, () -> AES.init().encrypt(value));
  }

  @DisplayName("Should Throw IllegalArgumentException when trying to decrypt Null values.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("illegalValuesResource")
  void throwIllegalArgumentExceptionOnDecryption(final String value) {
    assertThrows(IllegalArgumentException.class, () -> AES.init().decrypt(value));
  }

  private static Stream<Arguments> illegalValuesResource() {
    return Stream.of(Arguments.of(""), Arguments.of((Object) null));
  }

  @DisplayName("Should Throw AEADBadTagException when trying to decrypt with wrong Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("illegalKeyValuesResource")
  void shouldThrowAeadBadTagExceptionOnDecryption(final String input, final String key) {
    assertThrows(
        AEADBadTagException.class,
        () -> {
          final String encryptValue = AES.init(key).encrypt(input);
          AES.<String>init().decrypt(encryptValue);
        });
  }

  private static Stream<Arguments> illegalKeyValuesResource() {
    return Stream.of(
        Arguments.of("Should fail", "s0m3 K3y"), Arguments.of("Should Also fail", "4n0th37 k3Y"));
  }

  @DisplayName("Should successfully encrypt String type values With Default Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("stringEncryptionValues")
  void shouldEncryptStringTypeValues(final String input) throws Exception {
    final String encryptValue = AES.init().encrypt(input);
    final String decryptedValue = AES.<String>init().decrypt(encryptValue);

    assertEquals(decryptedValue, input);
  }

  @DisplayName("Should successfully encrypt String type values With Custom Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("stringEncryptionValues")
  void shouldEncryptStringTypeValuesWithCustomKey(final String input, final String key)
      throws Exception {
    final String encryptValue = AES.init(key).encrypt(input);
    final String decryptedValue = AES.<String>init(key).decrypt(encryptValue);

    assertTrue(decryptedValue.equalsIgnoreCase(input));
  }

  private static Stream<Arguments> stringEncryptionValues() {
    return Stream.of(
        Arguments.of("Encryption Test", "Xyx"), Arguments.of("GCM Encryption test", "A K3y"));
  }

  @DisplayName("Should successfully encrypt Integers type values With Default Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("intEncryptionValues")
  void shouldEncryptIntTypeValues(final int input) throws Exception {
    final String encryptValue = AES.init().encrypt(input);
    final int decryptedValue = AES.<Integer>init().decrypt(encryptValue);

    assertEquals(input, decryptedValue);
  }

  @DisplayName("Should successfully encrypt Integers type values With Custom Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("intEncryptionValues")
  void shouldEncryptIntTypeValuesWithCustomKey(final int input, final String key) throws Exception {
    final String encryptValue = AES.init(key).encrypt(input);
    final int decryptedValue = AES.<Integer>init(key).decrypt(encryptValue);

    assertEquals(input, decryptedValue);
  }

  private static Stream<Arguments> intEncryptionValues() {
    return Stream.of(
        Arguments.of("10020", "K3y"), Arguments.of("1929", "K37"), Arguments.of("-199", "620w37"));
  }

  @DisplayName("Should successfully encrypt Double type values With Default Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("doubleEncryptionValues")
  void shouldEncryptDoubleTypeValues(final double input) throws Exception {
    final String encryptValue = AES.init().encrypt(input);
    final double decryptedValue = AES.<Double>init().decrypt(encryptValue);

    assertEquals(input, decryptedValue);
  }

  @DisplayName("Should successfully encrypt Double type values With Custom Key.")
  @ParameterizedTest(name = "{index} => value={0}")
  @MethodSource("doubleEncryptionValues")
  void shouldEncryptDoubleTypeValuesWithCustomKey(double input, String key) throws Exception {
    final String encryptValue = AES.init(key).encrypt(input);
    final double decryptedValue = AES.<Double>init(key).decrypt(encryptValue);

    assertEquals(input, decryptedValue);
  }

  private static Stream<Arguments> doubleEncryptionValues() {
    return Stream.of(
        Arguments.of("10.020", "l0.p3zz"),
        Arguments.of("192.99", "l0p3zz"),
        Arguments.of("-1.99", "0p3zz"));
  }
}
