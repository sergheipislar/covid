import React from "react";
import PropTypes from "prop-types";
import { Chart } from "react-charts";

const StatisticsDashboardNrCasesBarChart = ({ records }) => {
  const uniqueDates = [...new Set(records.map((record) => record.date))].sort();

  const totalSum = records.reduce(
    (sum, record) => sum + record.numberOfCases,
    0
  );

  const totalByRegion = records.reduce((groups, record) => {
    const key = record.region.id;
    groups[key] = { ...(groups[key] || { regionId: key, numberOfCases: 0 }) };
    groups[key].numberOfCases += record.numberOfCases;
    return groups;
  }, {});

  const sortedTotalByRegion = Object.values(totalByRegion).sort(
    (r1, r2) => r2.numberOfCases - r1.numberOfCases
  );

  sortedTotalByRegion.forEach(
    (record, index) => (totalByRegion[record.regionId].top = index + 1)
  );

  const data = React.useMemo(() => {
    return uniqueDates.map((date) => ({
      label: date,
      datums: records
        .filter((record) => record.date === date)
        .sort(
          (r1, r2) =>
            totalByRegion[r2.region.id].numberOfCases -
            totalByRegion[r1.region.id].numberOfCases
        )
        .map((record) => ({
          x:
            totalByRegion[record.region.id].top +
            ". " +
            record.region.name +
            ` (${totalByRegion[record.region.id].numberOfCases})`,
          y: record.numberOfCases,
        })),
    }));
  });

  const series = React.useMemo(
    () => ({
      type: "bar",
    }),
    []
  );

  const axes = React.useMemo(() => [
    { primary: true, type: "ordinal", position: "left" },
    { position: "bottom", type: "linear", stacked: true },
  ]);

  return (
    <>
      <h2 style={{ marginLeft: "150px" }}>
        All
        <br />({totalSum})
      </h2>
      <Chart data={data} series={series} axes={axes} tooltip />
    </>
  );
};

StatisticsDashboardNrCasesBarChart.propTypes = {
  records: PropTypes.array.isRequired,
};

export default StatisticsDashboardNrCasesBarChart;
