/*******************************************************************************
 * Copyright (C) 2015 BOULMIER Jérôme, CORTIER Benoît
 *
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 *******************************************************************************/

package fr.utbm.tc.qlearningmario.mario.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;

import fr.utbm.tc.qlearningmario.mario.Levels;
import fr.utbm.tc.qlearningmario.mario.Scheduler;
import fr.utbm.tc.qlearningmario.mario.agent.MarioAgent;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SuppressWarnings({ "static-method", "unused" })
public class MainController implements Initializable {
	private Stage primaryStage;

	private Scheduler scheduler;

	private URL currentFileURL;

	private final Logger logger = Logger.getLogger(MainController.class.toString());

	@FXML
	private MenuBar mainMenuBar;

	@FXML
	private Label alphaLabel;

	@FXML
	private Label gammaLabel;

	@FXML
	private Label rhoLabel;

	@FXML
	private Label nuLabel;

	@FXML
	private Slider alphaSlider;

	@FXML
	private Slider gammaSlider;

	@FXML
	private Slider rhoSlider;

	@FXML
	private Slider nuSlider;

	@FXML
	private TextField mindLearningIterationsText;

	@FXML
	private ToggleGroup next_level_group;

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setStage(Stage stage) {
		this.primaryStage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.alphaSlider.valueProperty().addListener(
				(ChangeListener<? super Number>) (arg0, arg1, arg2) ->
				handleAlphaChange());

		this.gammaSlider.valueProperty().addListener(
				(ChangeListener<? super Number>) (arg0, arg1, arg2) ->
				handleGammaChange());

		this.rhoSlider.valueProperty().addListener(
				(ChangeListener<? super Number>) (arg0, arg1, arg2) ->
				handleRhoChange());

		this.nuSlider.valueProperty().addListener(
				(ChangeListener<? super Number>) (arg0, arg1, arg2) ->
				handleNuChange());
	}

	@FXML
	public void handleCloseAction(ActionEvent event) {
		this.primaryStage.fireEvent(new WindowEvent(this.primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	@FXML
	public void handleOpenFile(ActionEvent event) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(Locale.getString(this.getClass(), "fileChooser.window.title.load")); //$NON-NLS-1$
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("AI", "*.serai")); //$NON-NLS-1$ //$NON-NLS-2$


		this.scheduler.pause();

		final File file = fileChooser.showOpenDialog(this.primaryStage);

		if (file != null) {
			try {
				this.currentFileURL = file.toURI().toURL();
				enableSaveMenuItem();

				MarioAgent.loadQProblem(this.currentFileURL);
			} catch (final IOException | ClassNotFoundException e) {
				this.logger.severe(e.getMessage());
			}
		}

		this.scheduler.unpause();
	}

	@FXML
	public void handleSaveFile(ActionEvent event) {
		if (this.currentFileURL != null) {
			this.scheduler.pause();

			try {
				MarioAgent.saveQProblem(this.currentFileURL);
			} catch (final IOException e) {
				this.logger.severe(e.getMessage());
			}

			this.scheduler.unpause();
		}
	}

	@FXML
	public void handleSaveAsFile(ActionEvent event) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(Locale.getString(this.getClass(), "fileChooser.window.title.save")); //$NON-NLS-1$
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("AI", "*.serai")  //$NON-NLS-1$ //$NON-NLS-2$
				);

		this.scheduler.pause();

		final File file = fileChooser.showSaveDialog(this.primaryStage);

		if (file != null) {
			try {
				this.currentFileURL = file.toURI().toURL();
				enableSaveMenuItem();

				MarioAgent.saveQProblem(this.currentFileURL);
			} catch (final IOException e) {
				this.logger.severe(e.getMessage());
			}
		}

		this.scheduler.unpause();
	}

	@FXML
	public void handlePauseUnpause(ActionEvent event) {
		if (this.scheduler.isPaused()) {
			this.scheduler.unpause();
		} else {
			this.scheduler.pause();
		}
	}

	@FXML
	public void handleKillMario(ActionEvent event) {
		final MarioAgent marioAgent = this.scheduler.getMarioAgent();
		if (marioAgent != null) {
			marioAgent.getBody().kill();
		}
	}

	@FXML
	public void handleAlphaChange() {
		final double alpha = this.alphaSlider.getValue();
		final String strVal = Double.toString(alpha);
		this.alphaLabel.setText(strVal.substring(0, Math.min(4, strVal.length())));
		MarioAgent.getProblem().setAlpha((float) alpha);
	}

	@FXML
	public void handleGammaChange() {
		final double gamma = this.gammaSlider.getValue();
		final String strVal = Double.toString(gamma);
		this.gammaLabel.setText(strVal.substring(0, Math.min(4, strVal.length())));
		MarioAgent.getProblem().setGamma((float) gamma);
	}

	@FXML
	public void handleRhoChange() {
		final double rho = this.rhoSlider.getValue();
		final String strVal = Double.toString(rho);
		this.rhoLabel.setText(strVal.substring(0, Math.min(4, strVal.length())));
		MarioAgent.getProblem().setRho((float) rho);
	}

	@FXML
	public void handleNuChange() {
		final double nu = this.nuSlider.getValue();
		final String strVal = Double.toString(nu);
		this.nuLabel.setText(strVal.substring(0, Math.min(4, strVal.length())));
		MarioAgent.getProblem().setNu((float) nu);
	}

	@FXML
	public void handleTextField(KeyEvent event) {
		// Stops the event if the character is not a number.
		try {
			Integer.parseInt(event.getCharacter());
		} catch (final NumberFormatException e) {
			event.consume();
		}
	}

	@FXML
	public void handleMindLearn(ActionEvent event) {
		MarioAgent.mindLearn(Integer.parseInt(this.mindLearningIterationsText.getText()));
	}

	@FXML
	public void handleResetLevel(ActionEvent event) {
		switch (((RadioButton)this.next_level_group.getSelectedToggle()).getText()) {
			case "Level A": //$NON-NLS-1$
				this.scheduler.loadLevel(Levels.LEVEL_A);
				break;
			case "Level B": //$NON-NLS-1$
				this.scheduler.loadLevel(Levels.LEVEL_B);
				break;
			case "Level C": //$NON-NLS-1$
				this.scheduler.loadLevel(Levels.LEVEL_C);
				break;
			default :
				break;
		}
	}

	@FXML
	private void enableSaveMenuItem() {
		this.mainMenuBar.getMenus().get(0).getItems().get(1).setDisable(false);
	}
}
