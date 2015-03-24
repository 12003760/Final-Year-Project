package main.entity.spawner;

import main.entity.particle.Particle;
import main.graphics.Sprite;
import main.level.Level;

public class ParticleSpawner extends Spawner{
	
	@SuppressWarnings("unused")
	private int life;

	public ParticleSpawner(int x, int y, int life, int amount, Level level, Sprite sprite) {
		super(x, y, Type.PARTICLE, amount, level);
		this.life = life;
		for (int i = 0; i < amount; i++) {
			level.add(new Particle(x, y, life, sprite));
		}
	}
}
