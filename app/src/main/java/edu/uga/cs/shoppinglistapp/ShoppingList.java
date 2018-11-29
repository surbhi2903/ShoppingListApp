package edu.uga.cs.shoppinglistapp;

import java.util.List;

public class ShoppingList {
   private String id;
   private String title;
   private List<GroceryItem> items;


   public ShoppingList () {

   }

   public ShoppingList(String title, List<GroceryItem> items) {
       this.setId(id);
       this.setTitle(title);
       this.items = items;
   }

   public ShoppingList(String title) {
       this.title = title;
   }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setItems(List<GroceryItem> items) {
        this.items = items;
    }

    public List<GroceryItem> getItems() {
        return items;
    }
}
