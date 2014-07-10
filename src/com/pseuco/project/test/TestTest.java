package com.pseuco.project.test;

import org.junit.Assert;
import org.junit.Test;

import com.pseuco.project.Action;
import com.pseuco.project.Lts;
import com.pseuco.project.State;
import com.pseuco.project.Transition;

import java.util.LinkedList;
import java.util.List;

public class TestTest {

    @Test
    public void test() {
        List<State> states = new LinkedList<State>();
        State s1 = new State("A");
        states.add(s1);
        State s2 = new State("B");
        states.add(s2);

        List<Action> actions = new LinkedList<Action>();
        Action a = new Action("a");
        actions.add(a);

        List<Transition> transitions = new LinkedList<Transition>();
        Transition t = new Transition(s1, a, s2);
        transitions.add(t);

        Lts lts = new Lts(states, actions, transitions);
        System.out.println(lts);
        Assert.fail("Not yet implemented");
    }

}
