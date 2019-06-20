package pl.p.lodz.system.rodo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        return "Settings{" + "id=" + id + ", daysToDelete=" + daysToDelete + ", user=" + user.getId() + '}';
    }
}
