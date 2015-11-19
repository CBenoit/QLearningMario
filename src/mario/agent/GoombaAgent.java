package mario.agent;

import javafx.geometry.Point2D;
import mario.entity.Enemy;
import mario.entity.Entity;
import mario.entity.Goomba;

public class GoombaAgent extends Agent<Goomba> {
    public GoombaAgent(Goomba body) {
		super(body);
	}

	@Override
    public void update() {       
        if (getBody().getVelocity().getX() == 0.f) {
        	getBody().askAcceleration(new Point2D(-Goomba.maxAcceleration.getX(), 0));
            return;
        }
        
        for (Entity entity : getBody().getPerception()) {
            if (entity instanceof Enemy && getBody().collide(entity)) {
                if (getBody().getVelocity().getX() < 0) {
                	getBody().askAcceleration(new Point2D(Goomba.maxAcceleration.getX(), 0f));
                } else {
                	getBody().askAcceleration(new Point2D(-Goomba.maxAcceleration.getX(), 0f));
                }
            }
        }
    }
}
