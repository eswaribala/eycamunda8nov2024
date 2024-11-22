package com.ey.orderapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Product")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    @Id
    @Column(name="Product_Id")
    private long productId;
    @Column(name="Product_Name",length = 150,nullable = false)
    private String productName;
    @Column(name="Qty")
    private long qty;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "Order_Id"),
            name = "Order_Id_FK")
    private Order order;
}
