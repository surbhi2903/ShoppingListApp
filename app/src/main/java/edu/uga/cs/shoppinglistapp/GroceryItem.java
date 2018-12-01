package edu.uga.cs.shoppinglistapp;

public class GroceryItem {

    private String itemName;
    private String itemCost;
    private String purchasedBy;

    public GroceryItem() {

    }

   public GroceryItem(String itemName, String itemCost, String name) {
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.purchasedBy = name;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }

    public String getItemCost() {
        return itemCost;
    }

    public void setPurchasedBy(String purchasedBy) {
        this.purchasedBy = purchasedBy;
    }

    public String getPurchasedBy() {
        return purchasedBy;
    }
}
