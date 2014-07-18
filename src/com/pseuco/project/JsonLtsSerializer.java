package com.pseuco.project;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * Ermöglicht die Deserialisierung und Serialisierung von LTS im
 * pseuCo.com-LTS-JSON-Format.
 */
public class JsonLtsSerializer {
	public Lts deserialize(String input) {
		Collection<State> states = new HashSet<>();
		Collection<Transition> transitions = new HashSet<>();
		Collection<Action> actions = new HashSet<>();

		HashMap<String, State> strState= new HashMap<>();
		HashMap<State,JsonArray> stateTrans = new HashMap<>();

		State initialState;
		try {
			JsonObject ltsObject = Json.createReader(new StringReader(input))
					.readObject();
			initialState = new State(ltsObject.getString("initialState"));
			JsonObject statesObject = ltsObject.getJsonObject("states");
			Set<Entry<String,JsonValue>> stateSet = statesObject.entrySet();
			for (Entry<String,JsonValue> entry : stateSet) {
				JsonObject stateObject = (JsonObject) entry.getValue();
				State state = new State(entry.getKey());
				states.add(state);
				stateTrans.put(state, stateObject.getJsonArray("transitions"));
				strState.put(entry.getKey(), state);
			}
			for (State key : stateTrans.keySet()) {
				JsonArray transArr = stateTrans.get(key);
				for (JsonValue trans : transArr) {
					JsonObject transition = (JsonObject) trans;
					Action action = new Action(transition.getString("label"));
					String target = transition.getString("target");
					actions.add(action);
					transitions.add(new Transition(key, action, strState
							.get(target)));
				}
			}
		} catch (ClassCastException e) {
			throw new InputMismatchException();
		}
		return new Lts(states, actions, transitions, initialState);
	}

	public String serialize(Lts output) {
		Collection<State> states = output.getStates();
		Collection<Transition> transitions = output.getTransitions();
		JsonObjectBuilder statesBuilder = Json.createObjectBuilder();

		for(State state : states) {
			JsonArrayBuilder transBuilder = Json.createArrayBuilder();
			for(Transition trans : transitions) {
				if(trans.getSource().equals(state)) {
					transBuilder.add(Json.createObjectBuilder()
							.add("label", trans.getLabel().toString())
						    .add("detailsLabel", false)
						    .add("target", trans.getTarget().toString())
						    .build());
				}
			}
			JsonArray transArr = transBuilder.build();
			JsonObject stateObject = Json.createObjectBuilder()
					.add("transitions", transArr).build();
			statesBuilder.add(state.toString(), stateObject);
		}
		JsonObject statesObject = statesBuilder.build();

		JsonObject ltsObject = Json.createObjectBuilder()
			.add("initialState", output.getInitialState().toString())
			.add("states", statesObject)
			.build();
		return ltsObject.toString();
	}
}
