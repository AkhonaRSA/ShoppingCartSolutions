package snlftechapplication.snlftech;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class ShoppingItem {
    private final String itemName;
    private BigDecimal itemPrice;
    private int ItemQuantity;

    public ShoppingItem(String itemName, BigDecimal itemPrice, int itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.ItemQuantity = itemQuantity;
    }
    public String getItemName() {return itemName;}
    public BigDecimal getItemPrice() { return itemPrice;}
    public int getItemQuantity() {return ItemQuantity;}

    public void setItemPrice(BigDecimal newPrice) {
        this.itemPrice = newPrice;
    }
    public void addItemQuantity(int quantity) {
        ItemQuantity += quantity;
    }
    public BigDecimal getTotal(){
        return itemPrice.multiply(BigDecimal.valueOf(ItemQuantity));
    }
}
class ShoppingCart {
    // used an object specialized for cart operations instead of generic object type.
    private final Map<String, ShoppingItem> items = new HashMap<>();
    public void addItems(String name, BigDecimal price, int quantity) {
        //for this approach I chose to use the name as primary key, but for future reference generating key is the best approach
        ShoppingItem existingItem = items.get(name);

        if (existingItem != null) {
            if (existingItem.getItemPrice().compareTo(price) != 0) {
                existingItem.setItemPrice(price);
            }
            existingItem.addItemQuantity(quantity);
        } else {
            items.put(name, new ShoppingItem(name, price, quantity));
        }
    }
    public BigDecimal getTotal(){
        BigDecimal total = BigDecimal.ZERO;
        for(ShoppingItem item: items.values()) {
            total = total.add(item.getTotal());
        }
        return total;
    }
}

@RestController
@RequestMapping("/shop")
public class ShoppingCartController {
    private final Map<String, ShoppingCart> carts = new HashMap<>();
    @PostMapping("/addItem")
    public String addItem(@RequestParam("cartId") String cartId,
                          @RequestParam("itemName") String itemName,
                          @RequestParam("price") BigDecimal price,
                          @RequestParam("quantity") int quantity) {

        ShoppingCart cart = carts.computeIfAbsent(cartId, id -> new ShoppingCart());
        cart.addItems(itemName,price,quantity);

        return "Item added, Total is: " + cart.getTotal();
    }
    @GetMapping("/getTotal")
    public String getTotal(@RequestParam String cartId){
        ShoppingCart cart = carts.get(cartId);
        if(cart == null){
            return "Cart not found";
        }
        return "Total: " + cart.getTotal();
    }
}





























