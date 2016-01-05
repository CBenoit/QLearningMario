package fr.utbm.tc.qlearningmario.mario;

import org.arakhne.afc.vmutil.locale.Locale;

public enum Levels {
	LEVEL_A {
		@Override
		public String toString() {
			return Locale.getString(Levels.class, "Levels.A"); //$NON-NLS-1$
		}
	},

	LEVEL_B {
		@Override
		public String toString() {
			return Locale.getString(Levels.class, "Levels.B"); //$NON-NLS-1$
		}
	},


	LEVEL_C {
		@Override
		public String toString() {
			return Locale.getString(Levels.class, "Levels.C"); //$NON-NLS-1$
		}
	};
}
