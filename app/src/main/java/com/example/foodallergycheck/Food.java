package com.example.foodallergycheck;

public class Food {
        private String foodName;
        private String foodImageUrl;
        private String labelImageUrl;
        private String labelText;

        public Food() {
            // Required empty constructor for Firebase
        }

        public Food(String foodName, String foodImageUrl, String labelImageUrl, String labelText) {
            this.foodName = foodName;
            this.foodImageUrl = foodImageUrl;
            this.labelImageUrl = labelImageUrl;
            this.labelText = labelText;
        }

        public String getFoodName() {
            return foodName;
        }

        public String getFoodImageUrl() {
            return foodImageUrl;
        }

        public String getLabelImageUrl() {
            return labelImageUrl;
        }

        public String getLabelText() {
            return labelText;
        }
    }

