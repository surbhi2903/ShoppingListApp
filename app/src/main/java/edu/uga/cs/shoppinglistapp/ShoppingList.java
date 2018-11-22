package edu.uga.cs.shoppinglistapp;

public class ShoppingList {
   private String id;
   private String title;

   public ShoppingList(String title) {
       this.setId(id);
       this.setTitle(title);
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
}
