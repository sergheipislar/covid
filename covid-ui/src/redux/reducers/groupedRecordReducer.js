import * as types from "../actions/actionTypes";
import initialState from "./initialState";

export default function groupedRecordReducer(
  state = initialState.groupedRecords,
  action
) {
  switch (action.type) {
    case types.LOAD_GROUPED_RECORDS_SUCCESS:
      return action.groupedRecords;
    default:
      return state;
  }
}
