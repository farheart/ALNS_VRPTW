


## Lombok change hashCode() and make list.remove() failed

* Location
```Java
// GreedySolver.java

// Delivery closestDelivery = null;

// Remove customer from the non-served customers list.
this.instance.getDeliveryList().remove(closestDelivery);
```

```java
@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class Delivery extends AbstractService {
    private double amount;

    public Delivery(Delivery n) {
        this.id = n.id;
        ...
        this.timeWindow = n.timeWindow;
    }

    public Delivery() {
    }

}
```

If no `@EqualsAndHashCode(callSuper=true)`, then Delivery just use `amount` to differentiate instances without using other attributes defined in its parent class, which will cause problem in `list.remove()`

