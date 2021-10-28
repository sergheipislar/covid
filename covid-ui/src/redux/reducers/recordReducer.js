import * as types from "../actions/actionTypes";
import initialState from "./initialState";

export default function recordReducer(state = initialState.records, action) {
  switch (action.type) {
    case types.LOAD_RECORDS_SUCCESS:
      return action.records;
    default:
      return state;
  }
}
