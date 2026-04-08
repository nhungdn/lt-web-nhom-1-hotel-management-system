
package com.nhom1.hotelmanagement.dto;

public class CustomerDTO {

    //class trả ttin khách cho form booking
    public static class InfoForBooking{
        private String name;
        private String phone;
        private String idCard;

        public InfoForBooking(String name, String phone, String idCard) {
            this.name = name;
            this.phone = phone;
            this.idCard = idCard;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        
    }
}
