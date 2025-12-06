# 8️⃣ BDD Style - given/when/then

> **Test file:** [Part7_BDDStyleTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part7_BDDStyleTest.java)

---

## 🎯 BDD Style Enti?

**BDD = Behavior Driven Development**

Test structure:
- **Given** - Setup conditions
- **When** - Action performed
- **Then** - Verify expectations

Mockito provides `BDDMockito` class for BDD-style syntax:

| Standard | BDD Style |
|----------|-----------|
| `when().thenReturn()` | `given().willReturn()` |
| `verify()` | `then().should()` |

---

## 📝 BDD Syntax

### Stubbing

```java
// Standard Mockito
when(repo.findById(1L)).thenReturn(Optional.of(user));

// BDD Mockito
given(repo.findById(1L)).willReturn(Optional.of(user));
```

### Verification

```java
// Standard Mockito
verify(repo).save(user);
verify(repo, never()).delete(any());

// BDD Mockito
then(repo).should().save(user);
then(repo).should(never()).delete(any());
```

---

## 🏠 Complete BDD Test

```java
@Test
void should_send_welcome_email_when_user_registers() {
    // GIVEN
    given(userRepository.existsByEmail("test@email.com")).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(savedUser);
    given(emailService.sendWelcomeEmail(anyString(), anyString())).willReturn(true);
    
    // WHEN
    User result = userService.registerUser("Test", "test@email.com", "pass123");
    
    // THEN
    then(userRepository).should().save(any(User.class));
    then(emailService).should().sendWelcomeEmail("test@email.com", "Test");
    assertNotNull(result);
}
```

---

## 📎 Related Files

- **Test examples:** [Part7_BDDStyleTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part7_BDDStyleTest.java)
