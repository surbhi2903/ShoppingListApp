package edu.uga.cs.shoppinglistapp;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShoppingList {
    private String uid;
    private String title;
    private List<GroceryItem> items;
    private String listId;
    private String shoppingId;
    private List<String> users;

    public ShoppingList () {

    }

    public ShoppingList(String title, List<GroceryItem> items) {
        this.setUid(uid);
        this.setTitle(title);
        this.items = items;
    }

    public String getShoppingId() {return  shoppingId; }

    public void setShoppingId(String shoppingId) { this.shoppingId = shoppingId; }

    public String getListId() { return listId; }

    public void setListId(String listId) { this.listId = listId; }

    public ShoppingList(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
