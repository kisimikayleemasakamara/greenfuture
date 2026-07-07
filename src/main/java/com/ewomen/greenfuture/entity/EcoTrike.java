package com.ewomen.greenfuture.entity;

import jakarta.persistence.*;

@Entity
public class EcoTrike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trikeCode;

    @Enumerated(EnumType.STRING)
    private EcoTrikeStatus status;

    private Double batteryLevel;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    public Long getId() {
        return id;
    }

    public String getTrikeCode() {
        return trikeCode;
    }

    public void setTrikeCode(String trikeCode) {
        this.trikeCode = trikeCode;
    }

    public EcoTrikeStatus getStatus() {
        return status;
    }

    public void setStatus(EcoTrikeStatus status) {
        this.status = status;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }
}