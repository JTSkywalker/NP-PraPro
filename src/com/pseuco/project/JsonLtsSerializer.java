package com.pseuco.project;

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class JsonLtsSerializer {
	public Lts deserialize(String input) {
		Collection<State> states = new LinkedList<>();
		Collection<Transition> transitions = new LinkedList<>();
		Collection<Action> actions = new ConcurrentSkipListSet<>();
		
		JsonObject ltsObject = Json.createReader(new StringReader(input)).readObject();
		State initialState = new State(ltsObject.getString("initialState"));
		JsonObject statesObject = ltsObject.getJsonObject("states");
		for (String state : statesObject.keySet()) {

			JsonObject stateObject = statesObject.getJsonObject(state);

			JsonArray transitionsArray = stateObject.getJsonArray("transitions");

			for (int i = 0; i < transitionsArray.size(); i++) {

				JsonObject transition = transitionsArray.getJsonObject(i);

				Action action = new Action(transition.getString("label"));
				String target = transition.getString("target");
				actions.add(action);
				

				// TODO decide whether to do something about it, else remove
				// String detailsLabel = null;
				// try {
				// 	detailsLabel = transition.getString("detailsLabel");
				//} catch (ClassCastException e) {
					// ignore - detailsLabel = null
				//}

			}

		}
		return new Lts(states, actions, transitions, initialState);
	}
	
	public String serialize(Lts output) {
		throw new UnsupportedOperationException();
	}
}
