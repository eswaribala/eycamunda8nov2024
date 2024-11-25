package com.ey.orderapi.models;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="WebStore_Stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Stock_Id")
    private long stockId;
    @Column(name = "Product_Id")
    private long productId;
    @Column(name = "Available_Qty")
    private long availableQty;
    @Column(name = "Location",nullable = false,length = 150)
    private String location;
}
