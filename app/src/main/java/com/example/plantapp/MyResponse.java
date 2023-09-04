package com.example.plantapp;

import java.util.List;

public class MyResponse {
    public List<ItemListElement> itemListElement;

    public static class ItemListElement {
        public Result result;
    }

    public static class Result {
        public String name;
        public String description;
        public DetailedDescription detailedDescription;
    }

    public static class DetailedDescription {
        public String url;
        public String articleBody;
    }
}
