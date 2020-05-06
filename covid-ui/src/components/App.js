import React from "react";
import { Route, Switch } from "react-router-dom";
import StatisticsPage from "./statistics/StatisticsPage";
import StatisticsDashboard from "./statistics/StatisticsDashboard";
import Header from "./common/Header";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const App = () => {
  return (
    <div className="container-fluid">
      <Header />
      <Switch>
        <Route exact path="/" component={StatisticsDashboard} />
        <Route path="/statistics" component={StatisticsPage} />
      </Switch>
      <ToastContainer autoClose={3000} hideProgressBar />
    </div>
  );
};

export default App;
