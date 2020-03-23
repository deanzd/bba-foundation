package com.eking.momp.db.model;

import com.eking.momp.db.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Dean
 * @since 2019-06-12
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelRelation extends BaseModel<ModelRelation> {

	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String name;

	private Integer relationTypeId;

	private Mapping mapping;

	private Integer modelId;

	private Integer targetModelId;
	
	private Integer modelFieldId;
	
	private String description;

	@Getter
	public enum Mapping {
		OneToOne("1:1"),
		OneToMany("1:N"),
		ManyToOne("N:1"),
		ManyToMany("N:N");

		private final String text;

		Mapping(String text) {
			this.text = text;
		}
	}

}
