package edu.tcu.cs.hogwartsartifactsonline.artifact;

import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        artifacts = new ArrayList<>();
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");
        this.artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");
        this.artifacts.add(a2);

        this.artifacts = new ArrayList<>();
        artifacts.add(a1);
        artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object artifactRepository
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904191");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        artifact.setOwner(w);

        given(artifactRepository.findById("1250808601744904191")).willReturn(Optional.of(artifact)); // Defines the behavior of the mock object

        // When. Act on the target behavior. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById("1250808601744904191");

        // Then. Assert expected outcomes.
        assertThat(returnedArtifact.getId()).isEqualTo(artifact.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());

        verify(artifactRepository, times(1)).findById("1250808601744904191"); // verify that mock object method is called exactly once
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> {
            Artifact returnedArtifact = artifactService.findById("1250808601744904191");
        });

        // Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class).hasMessage("Could not find artifact with Id 1250808601744904191 :(");
        verify(artifactRepository, times(1)).findById("1250808601744904191"); // verify that mock object method is called exactly once
    }

    @Test
    void testFindAllSuccess() {
        // Given
        given(artifactRepository.findAll()).willReturn(this.artifacts);

        // When
        List<Artifact> actualArtifacts = artifactService.findAll();

        // Then
        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        // Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description...");
        newArtifact.setImageUrl("ImageUrl...");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        // When
        Artifact savedArtifact = artifactService.save(newArtifact);

        // Then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904191");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        oldArtifact.setImageUrl("ImageUrl");

        Artifact update = new Artifact();
        //update.setId("1250808601744904191");
        update.setName("Invisibility Cloak");
        update.setDescription("A new description."); // only modifying description. Cannot modify primary key (Id)
        update.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904191")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        // When
        Artifact updatedArtifact = artifactService.update("1250808601744904191", update);

        // Then
        assertThat(updatedArtifact.getId()).isEqualTo("1250808601744904191");
        assertThat(updatedArtifact.getName()).isEqualTo(update.getName());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        assertThat(updatedArtifact.getImageUrl()).isEqualTo(update.getImageUrl());
        verify(artifactRepository, times(1)).findById("1250808601744904191");
        verify(artifactRepository, times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound() {
        // Given
        Artifact update = new Artifact();
        update.setId("1250808601744904191");
        update.setName("Invisibility Cloak");
        update.setDescription("A new description."); // only modifying description. Cannot modify primary key (Id)
        update.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904191")).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> artifactService.update("1250808601744904191", update));

        // Then
        verify(artifactRepository, times(1)).findById("1250808601744904191");
    }

    @Test
    void testDeleteSuccess() {
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904193");
        artifact.setName("Elder Wand");
        artifact.setDescription("The Elder Wand, known throughout history as the Deathstick or the Wand of Destiny, is an extremely powerful wand made of elder wood with a core of Thestral tail hair");
        artifact.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904193")).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("1250808601744904193");

        // When
        artifactService.delete("1250808601744904193");

        // Then
        verify(artifactRepository, times(1)).deleteById("1250808601744904193");

    }

    @Test
    void testDeleteNotFound() {
        // Given
        given(artifactRepository.findById("1250808601744904193")).willReturn(Optional.empty());

        // When and Then
        assertThrows(ObjectNotFoundException.class, () -> artifactService.delete("1250808601744904193"));
    }
}