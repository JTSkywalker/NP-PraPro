package com.pseuco.project.test;

import isomorph.IsoChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pseuco.project.Action;
import com.pseuco.project.JsonLtsSerializer;
import com.pseuco.project.Lts;
import com.pseuco.project.LtsMinimizer;
import com.pseuco.project.State;
import com.pseuco.project.Transition;

public class LtsMinimizerTest {

	private static Lts ltsA;
	private static State s1, s2, s3, s4, s5, s6;
	private static Action i, a, b;

	private final File dataDir = new File("test");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		s1 = new State("A");
		s2 = new State("B");
		s3 = new State("C");
		s4 = new State("D");
		s5 = new State("E");
		s6 = new State("F");
		i = Action.INTERNAL;
		a = new Action("a");
		b = new Action("b");

		ltsA = new Lts(s1);
		ltsA.addState(s1);
		ltsA.addState(s2);
		ltsA.addState(s3);
		ltsA.addState(s4);
		ltsA.addState(s5);
		ltsA.addState(s6);
		ltsA.addAction(i);
		ltsA.addAction(a);
		ltsA.addAction(b);

		ltsA.addTransition(new Transition(s1, a, s3));
		ltsA.addTransition(new Transition(s1, i, s4));

		ltsA.addTransition(new Transition(s2, i, s1));
		ltsA.addTransition(new Transition(s2, a, s5));

		ltsA.addTransition(new Transition(s3, i, s6));

		ltsA.addTransition(new Transition(s4, a, s6));
		ltsA.addTransition(new Transition(s4, i, s2));

		ltsA.addTransition(new Transition(s5, b, s6));
	}

	@Test
	public void test() throws InterruptedException {
		for (File inFile : dataDir.listFiles()) {
			try {
				String name = inFile.getName();
				if (name.endsWith("in")) {
					String baseName = name.substring(0, name.lastIndexOf("."));
					String resultFileName = baseName + ".res";
					Path resultFilePath = Paths.get(inFile.getParent() + "/"
							+ resultFileName);
					String in = new String(Files.readAllBytes(inFile.toPath()));
					String expectedRes = new String(
							Files.readAllBytes(resultFilePath));
					LtsMinimizer minimizer = new LtsMinimizer();
					JsonLtsSerializer serializer = new JsonLtsSerializer();
					IsoChecker.assertIsomorphic(expectedRes, serializer
							.serialize(minimizer.minimize(serializer
									.deserialize(in))));
				}
			} catch (IOException e) {

			}
		}
	}

}
