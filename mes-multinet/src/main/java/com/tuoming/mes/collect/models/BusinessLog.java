package com.tuoming.mes.collect.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

@Entity
@Table(name = "mes_business_log_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BusinessLog extends AbstractModel {
    @Column(name = "timestamp", length = 128, nullable = false)
    private String timestamp;

    @Column(name = "module_type", nullable = false)
    private Integer module_type;

    @Column(name = "content", length = 128, nullable = false)
    private String content;

    @Column(name = "result", nullable = false)
    private Integer result;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getModule_type() {
        return module_type;
    }

    public void setModule_type(Integer module_type) {
        this.module_type = module_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

}
