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

package fr.utbm.tc.qlearningmario.mario.agent;

import java.io.IOException;
import java.net.URL;

import org.arakhne.afc.vmutil.locale.Locale;

import fr.utbm.tc.qlearningmario.mario.entity.MarioBody;
import fr.utbm.tc.qlearningmario.qlearning.QAction;
import fr.utbm.tc.qlearningmario.qlearning.QLearning;
import javafx.geometry.Point2D;

/** The MarioBody's corresponding agent.
 *
 * <p>It makes use of the Q-Learning engine.
 *
 * @author $Author: boulmier$
 * @author $Author: cortier$
 * @mavengroupid $GroupId$
 * @version $FullVersion$
 * @mavenartifactid $ArtifactId$
 */
public class MarioAgent extends Agent<MarioBody> {
	private static final int NB_LEARNING_ITERATIONS;

	static {
		NB_LEARNING_ITERATIONS = Integer.parseInt(Locale.getString(MarioAgent.class, "number.learning.iterations")); //$NON-NLS-1$
	}

	private static final MarioProblem PROBLEM = new MarioProblem();

	private static QLearning<MarioProblem> QLEARNING = new QLearning<>(PROBLEM);

	/** Initialize the agent with the given MarioBody.
	 *
	 * @param body : a MarioBody.
	 */
	public MarioAgent(MarioBody body) {
		super(body);
	}

	public static void saveQProblem(URL fileName) throws IOException {
		synchronized (MarioAgent.QLEARNING) {
			MarioAgent.QLEARNING.saveQValues(fileName);
		}
	}

	public static void loadQProblem(URL fileName) throws IOException, ClassNotFoundException {
		synchronized (MarioAgent.QLEARNING) {
			MarioAgent.QLEARNING.loadQValues(fileName);
		}
	}

	/** Makes the agent learn on its own without updating world state.
	 *
	 * @param nbIterations
	 */
	public static void mindLearn(int nbIterations) {
		synchronized (MarioAgent.QLEARNING) {
			MarioAgent.QLEARNING.learn(nbIterations);
		}
	}

	/** Get the MarioProblem.
	 *
	 * @return MarioProblem
	 */
	public static MarioProblem getProblem() {
		return MarioAgent.PROBLEM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void live() {
		super.live();

		final MarioProblem.Action action;

		MarioAgent.PROBLEM.translateCurrentState(getBody(), getBody().getPerception());
		synchronized (MarioAgent.QLEARNING) {
			MarioAgent.QLEARNING.learn(NB_LEARNING_ITERATIONS);

			final QAction qAction = MarioAgent.QLEARNING.getBestAction(MarioAgent.PROBLEM.getCurrentState());
			action = MarioProblem.Action.fromQAction(qAction);
		}

		if (action == MarioProblem.Action.JUMP) {
			getBody().askAcceleration(new Point2D(0, -getBody().getMaxAcceleration().getY()));
		} else if (action == MarioProblem.Action.MOVE_LEFT) {
			getBody().askAcceleration(new Point2D(-getBody().getMaxAcceleration().getX(), 0));
		} else if (action == MarioProblem.Action.MOVE_RIGHT) {
			getBody().askAcceleration(new Point2D(getBody().getMaxAcceleration().getX(), 0));
		} else {
			getBody().askAcceleration(new Point2D(-getBody().getVelocity().getX(), 0));
		}
	}

	public static void reset() {
		QLEARNING = new QLearning<>(PROBLEM);
	}
}
