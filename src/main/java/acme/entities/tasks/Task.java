
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.realms.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//@Table(indexes = {
//		@Index(columnList = "technician_id"), @Index(columnList = "draftMode"), @Index(columnList = "technician_id, draftMode")
//	})
public class Task extends AbstractEntity {

	private static final long	serialVersionUID	=  1L;

	@Mandatory
	@Valid
	@Automapped
	private TaskType			type;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Integer				priority;

	@Mandatory
	@ValidNumber(min = 0, max = 700000)
	@Automapped
	private Integer				duration;

	@ManyToOne
	@Mandatory
	@Automapped
	private Technician			technician;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draftMode;

}
