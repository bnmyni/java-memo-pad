package com.scoco.wms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "t_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Integer id;

    @Version
    private Integer version;

    private String productId;

    private String description;

    private String imageUrl;

    private BigDecimal price;

}
