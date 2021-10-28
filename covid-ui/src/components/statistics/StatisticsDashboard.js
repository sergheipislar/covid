import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import * as regionActions from "../../redux/actions/regionActions";
import * as recordApi from "../../api/recordApi";
import PropTypes from "prop-types";
import { bindActionCreators } from "redux";
import { calculateTodayDateWithOffsetInSqlFormat } from "./Utils";
import StatisticsDashboardDifferenceBarChart from "./StatisticsDashboardDifferenceBarChart";
import StatisticsDashboardNrCasesBarChart from "./StatisticsDashboardNrCasesBarChart";

const StatisticsPage = ({ regions, actions }) => {
  const [recordsToday, setRecordsToday] = useState([]);
  const [recordsWeek, setRecordsWeek] = useState([]);

  const today = calculateTodayDateWithOffsetInSqlFormat(0);
  const aWeekAgo = calculateTodayDateWithOffsetInSqlFormat(-7);

  useEffect(() => {
    if (regions.length === 0) {
      actions.loadRegions().catch((error) => {
        alert("Loading regions failed " + error);
      });
    }
  });

  useEffect(() => {
    recordApi
      .getRecordsFromLastDay()
      .then((records) => setRecordsToday(records))
      .catch((error) => {
        alert("Loading records failed " + error);
      });
  }, []);

  useEffect(() => {
    recordApi
      .getRecordsByDate(aWeekAgo, today)
      .then((records) => setRecordsWeek(records))
      .catch((error) => {
        alert("Loading records failed " + error);
      });
  }, []);

  return (
    <div style={{ marginTop: "10px", display: "flex" }}>
      {recordsToday.length !== 0 ? (
        <div style={{ width: "400px", height: "800px" }}>
          <StatisticsDashboardDifferenceBarChart records={recordsToday} />
        </div>
      ) : (
        ""
      )}
      {recordsToday.length !== 0 ? (
        <div style={{ width: "400px", height: "800px" }}>
          <StatisticsDashboardNrCasesBarChart records={recordsToday} />
        </div>
      ) : (
        ""
      )}
      {recordsWeek.length !== 0 ? (
        <div style={{ width: "800px", height: "800px", marginLeft: "50px" }}>
          <StatisticsDashboardDifferenceBarChart records={recordsWeek} />
        </div>
      ) : (
        ""
      )}
    </div>
  );
};

StatisticsPage.propTypes = {
  regions: PropTypes.array.isRequired,
  actions: PropTypes.object.isRequired,
};

function mapStateToProps(state) {
  return {
    regions: state.regions,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      loadRegions: bindActionCreators(regionActions.loadRegions, dispatch),
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(StatisticsPage);
