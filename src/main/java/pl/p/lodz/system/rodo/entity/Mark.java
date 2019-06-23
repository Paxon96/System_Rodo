package pl.p.lodz.system.rodo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@Table(name = "Mark")
@NoArgsConstructor
@AllArgsConstructor
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "MarkID")
    private int id;

    @Column(name = "Activity")
    private String activity;

    @Column(name = "Mark")
    private double mark;

    @Column(name = "EvalDate")
    private Timestamp evalDate;

    @Column(name = "Points")
    private double points;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "StudentID")
    private User user;

    @Override
    public String toString() {
        return "Mark{" + "id=" + id + ", mark=" + mark + ", evalDate=" + evalDate + ", points=" + points + ", user=" + user.getId() + '}';
    }
}
