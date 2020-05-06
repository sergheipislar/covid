import { createStore, applyMiddleware } from "redux";
import routeReducer from "./reducers";
import thunk from "redux-thunk";

export default function configureStore(initialState) {
  return createStore(routeReducer, initialState, applyMiddleware(thunk));
}
