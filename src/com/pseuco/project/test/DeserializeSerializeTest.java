package com.pseuco.project.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pseuco.project.*;

public class DeserializeSerializeTest {
	
	private static Lts lts;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		Set<State> states = new HashSet<>(3);
		Set<Action> actions = new HashSet<>(3);
		Set<Transition> transs = new HashSet<>(3);

		String abStopPlusτStop = "a.b.0 + τ.0";
		String bStop = "b.0";
		String stop = "0";
		String a = "a";
		String b = "b";
		String τ = "τ";
		states.add(new State(abStopPlusτStop));
		states.add(new State(bStop));
		states.add(new State(stop));
		actions.add(new Action(a));
		actions.add(new Action(b));
		actions.add(new Action(τ));
		transs.add(new Transition(new State(abStopPlusτStop),
                new Action(a),
                new State(bStop)));
		transs.add(new Transition(new State(abStopPlusτStop),
                new Action(τ),
                new State(stop)));
		transs.add(new Transition(new State(bStop),
                new Action(b),
                new State(stop)));
		
		lts = new Lts(states, actions, transs, new State(abStopPlusτStop));
	}
	
	
	@Test
	public void testDeserialize() {//TODO repair
		try {
			Scanner scanner = new Scanner(new File("SerDeserTestLts.json"));
			String content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			JsonLtsSerializer jls = new JsonLtsSerializer();
			assertEquals(jls.deserialize(content), lts);				
		} catch (FileNotFoundException e) {
			fail("test broken (FileNotFoundException)");
		}
	}

	@Test
	public void testSerialize() {
		JsonLtsSerializer jls = new JsonLtsSerializer();
		System.out.println(jls.serialize(lts).toString());
		assertEquals(jls.deserialize(jls.serialize(lts)), lts);
	}
}
