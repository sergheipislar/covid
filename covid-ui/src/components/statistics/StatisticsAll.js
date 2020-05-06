import React from "react";
import PropTypes from "prop-types";
import { Chart } from "react-charts";

const StatisticsAll = ({ records }) => {
  const convertSqlDateToJavascriptDate = (sqlDate) => {
    const [year, month, day] = sqlDate.split("-");
    return new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
  };

  const dataToDisplay = records.map((record, index) => {
    return [
      convertSqlDateToJavascriptDate(record.date),
      record.numberOfCases,
      index !== 0 ? record.numberOfCases - records[index - 1].numberOfCases : 0,
    ];
  });

  const data = React.useMemo(
    () => [
      {
        label: "Series 1",
        data: dataToDisplay,
      },
    ],
    [records]
  );

  const axes = React.useMemo(
    () => [
      { primary: true, type: "time", position: "bottom" },
      { type: "linear", position: "left" },
    ],
    [records]
  );

  // eslint-disable-next-line react/prop-types
  const CustomTooltip = ({ datum }) => {
    return datum ? (
      <div>
        Date: {datum.originalDatum[0].toLocaleDateString("ro-RO")}
        <br />
        Current: {datum.originalDatum[1]}
        <br />
        Difference: {datum.originalDatum[2]}
      </div>
    ) : (
      ""
    );
  };

  const tooltip = React.useMemo(
    () => ({
      // eslint-disable-next-line
      render: ({ datum }) => {
        return <CustomTooltip {...{ datum }} />;
      },
    }),
    []
  );

  return (
    <div>
      {records.length !== 0 ? (
        <div>
          <div style={{ width: "800px", height: "500px" }}>
            <Chart data={data} axes={axes} tooltip={tooltip} />
          </div>
        </div>
      ) : (
        ""
      )}
    </div>
  );
};

StatisticsAll.propTypes = {
  records: PropTypes.array.isRequired,
};

export default StatisticsAll;
