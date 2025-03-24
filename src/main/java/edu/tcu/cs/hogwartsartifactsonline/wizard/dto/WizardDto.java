package edu.tcu.cs.hogwartsartifactsonline.wizard.dto;

import jakarta.validation.constraints.NotEmpty;

public record WizardDto(Integer id,
                        String name,
                        Integer numberOfArtifacts) {


}
