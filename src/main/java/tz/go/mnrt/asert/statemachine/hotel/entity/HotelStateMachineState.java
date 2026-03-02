package tz.go.mnrt.asert.statemachine.hotel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "state_machine_states")
public class HotelStateMachineState {

    @Id
    private String stateMachineId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String state;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String event;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contextObj;

}
