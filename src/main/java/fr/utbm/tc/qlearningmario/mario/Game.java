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

package fr.utbm.tc.qlearningmario.mario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.locale.Locale;

import fr.utbm.tc.qlearningmario.mario.entity.World;
import fr.utbm.tc.qlearningmario.mario.ui.MainController;
import fr.utbm.tc.qlearningmario.mario.ui.MarioGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/** Main class of the application.
 *
 * @author $Author: boulmier$
 * @author $Author: cortier$
 * @mavengroupid $GroupId$
 * @version $FullVersion$
 * @mavenartifactid $ArtifactId$
 */
public class Game extends Application {
	public static int SCENE_HEIGHT;

	public static int SCENE_WIDTH;

	public static final int SCALE;

	private static final int NUMBER_OF_THREAD = 2;

	static {
		SCALE = Integer.parseInt(Locale.getString(Game.class, "scene.scale")); //$NON-NLS-1$
	}

	private final Logger log = Logger.getLogger(Game.class.getName());

	@Override
	public void start(Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Resources.getResource(getClass(), "fr/utbm/tc/qlearningmario/MainWindow.fxml")); //$NON-NLS-1$

			final Pane root = (Pane) loader.load();
			final MainController mainController = (MainController)loader.getController();

			mainController.setStage(primaryStage);

			final Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

			primaryStage.setScene(scene);
			primaryStage.setTitle(Locale.getString(getClass(), "frame.title")); //$NON-NLS-1$

			final Canvas canvas = (Canvas) scene.lookup("#marioCanvas"); //$NON-NLS-1$

			SCENE_WIDTH =  (int) canvas.getWidth();
			SCENE_HEIGHT = (int) canvas.getHeight();

			final GraphicsContext gc = canvas.getGraphicsContext2D();

			final World world = new World();

			final MarioGUI gui = new MarioGUI(gc);
			world.addWorldListener(gui);
			gui.start();

			final Scheduler scheduler = new Scheduler(world);
			mainController.setScheduler(scheduler);
			world.addWorldListener(scheduler);

			final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);

			// Run the scheduler.
			executor.execute(scheduler);
			executor.shutdown();

			primaryStage.show();

			primaryStage.setOnCloseRequest(
					(WindowEvent we) -> {
						this.log.info(Locale.getString(Game.this.getClass(), "closing.stage")); //$NON-NLS-1$
						scheduler.stop();
					});
		} catch (final Exception e) {
			this.log.severe(e.toString());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
