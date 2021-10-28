import { combineReducers } from "redux";
import regions from "./regionReducer";
import records from "./recordReducer";
import groupedRecords from "./groupedRecordReducer";
import apiCallsInProgress from "./apiStatusReducer";

const routeReducer = combineReducers({
  regions,
  records,
  groupedRecords,
  apiCallsInProgress,
});

export default routeReducer;
