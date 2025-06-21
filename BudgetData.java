package com.example.wedwise_java;

import java.util.ArrayList;
import java.util.List;

public class BudgetData {
    // Static variable to store the total budget amount
    public static int totalBudget = 0;

    // Static variables to store spent and remaining budget
    public static int spentBudget = 0;
    public static int remainingBudget = 0;

    // Static list to store all budget categories
    public static List<BudgetCategory> categories = new ArrayList<>();

    // Class to represent a budget category
    public static class BudgetCategory {
        public String name;
        public List<BudgetItem> items = new ArrayList<>();

        public BudgetCategory(String name) {
            this.name = name;
        }
    }

    // Class to represent an individual budget item inside a category
    public static class BudgetItem {
        public String name;
        public int amount;

        public BudgetItem(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}