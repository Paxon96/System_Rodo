package pl.p.lodz.system.rodo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "daysToDelete")
    private short daysToDelete;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "UserID")
    private User user;
}
