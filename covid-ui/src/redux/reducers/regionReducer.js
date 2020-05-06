import * as types from "../actions/actionTypes";
import initialState from "./initialState";

export default function regionReducer(state = initialState.regions, action) {
  switch (action.type) {
    case types.LOAD_REGIONS_SUCCESS:
      return action.regions;
    default:
      return state;
  }
}
