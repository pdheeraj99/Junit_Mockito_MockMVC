# 📖 Mockito BDD Style

> **Mawa, Given-When-Then style lo tests raayali? BDDMockito use chey!**

---

## 🤔 What is BDD Style?

```java
// Traditional Mockito
when(repository.findById(1L)).thenReturn(user);
verify(repository).findById(1L);

// BDD Mockito (more readable!)
given(repository.findById(1L)).willReturn(user);
then(repository).should().findById(1L);
```

**BDD = Behavior Driven Development**
- Given: Setup (preconditions)
- When: Action (what we test)
- Then: Assertion (expected result)

---

## 📋 BDD Equivalents

| Traditional | BDD Style |
|-------------|-----------|
| `when().thenReturn()` | `given().willReturn()` |
| `when().thenThrow()` | `given().willThrow()` |
| `when().thenAnswer()` | `given().willAnswer()` |
| `doReturn().when()` | `willReturn().given()` |
| `verify()` | `then().should()` |
| `verify(never())` | `then().should(never())` |
| `verifyNoInteractions()` | `then().shouldHaveNoInteractions()` |

---

## 💻 Code Examples

### 1️⃣ Complete BDD Test

```java
import static org.mockito.BDDMockito.*;

@Test
void shouldRegisterUser_BDDStyle() {
    // Given (Setup)
    given(repository.existsByEmail("john@test.com")).willReturn(false);
    given(repository.save(any(User.class))).willReturn(savedUser);
    
    // When (Action)
    User result = userService.register("John", "john@test.com");
    
    // Then (Assertions & Verifications)
    assertThat(result.getName()).isEqualTo("John");
    
    then(repository).should().existsByEmail("john@test.com");
    then(repository).should().save(any(User.class));
    then(emailService).should().sendWelcome("john@test.com");
}
```

### 2️⃣ BDD Exception

```java
@Test
void shouldThrowWhenEmailExists_BDDStyle() {
    // Given
    given(repository.existsByEmail("exists@test.com")).willReturn(true);
    
    // When & Then
    assertThatThrownBy(() -> 
        userService.register("John", "exists@test.com"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already registered");
    
    then(repository).should(never()).save(any());
}
```

### 3️⃣ BDD Void Methods

```java
@Test
void shouldDeleteUser_BDDStyle() {
    // Given
    willDoNothing().given(repository).deleteById(anyLong());
    
    // When
    userService.delete(1L);
    
    // Then
    then(repository).should().deleteById(1L);
    then(auditService).should().log(any(AuditLog.class));
}

@Test
void shouldHandleDeleteError_BDDStyle() {
    // Given
    willThrow(new DataAccessException("DB error"))
        .given(repository).deleteById(anyLong());
    
    // When & Then
    assertThatThrownBy(() -> userService.delete(1L))
        .isInstanceOf(ServiceException.class);
}
```

---

## 📊 Comparison

```
TRADITIONAL:
┌────────────────────────────────────────────────────┐
│ when(mock.method()).thenReturn(value);             │
│ // ... test code ...                               │
│ verify(mock).method();                             │
└────────────────────────────────────────────────────┘

BDD STYLE:
┌────────────────────────────────────────────────────┐
│ // GIVEN                                           │
│ given(mock.method()).willReturn(value);            │
│                                                    │
│ // WHEN                                            │
│ // ... test code ...                               │
│                                                    │
│ // THEN                                            │
│ then(mock).should().method();                      │
└────────────────────────────────────────────────────┘
```

---

## 🏢 Enterprise Pattern

```java
@Test
void shouldProcessOrder_BDDStyle() {
    // Given - Customer exists
    given(customerRepo.findById(1L)).willReturn(Optional.of(customer));
    
    // Given - Items in stock
    given(inventoryService.checkStock(anyList())).willReturn(true);
    
    // Given - Payment succeeds
    given(paymentService.charge(any(), any())).willReturn(receipt);
    
    // When
    Order result = orderService.placeOrder(1L, items);
    
    // Then - Order created
    assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    
    // Then - Correct sequence of operations
    InOrder inOrder = inOrder(inventoryService, paymentService, orderRepo);
    
    then(inventoryService).should(inOrder).checkStock(anyList());
    then(paymentService).should(inOrder).charge(any(), any());
    then(orderRepo).should(inOrder).save(any());
    
    // Then - Notification sent
    then(notificationService).should().sendOrderConfirmation(any());
}
```

---

## 🎯 When to Use BDD Style?

| Use BDD When | Use Traditional When |
|--------------|---------------------|
| Team follows BDD | Quick/simple tests |
| Readability priority | Familiarity needed |
| Business-readable tests | Performance critical |
| New projects | Legacy projects |

---

## 😂 Memory Trick

```
BDD = "Given When Then"

given() = "GIVEN idi setup"
when = "WHEN action jarigindi"  // (this is your actual code, not Mockito)
then() = "THEN idi jarugali"

given().willReturn() = when().thenReturn()
then().should() = verify()

"will" instead of "then" = BDD style!
```

---

## 🔗 Related Topics

- [Stubbing](./03-stubbing.md) - when/thenReturn
- [Verification](./04-verification.md) - verify()
- [Your Code](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part7_BDDStyleTest.java)
