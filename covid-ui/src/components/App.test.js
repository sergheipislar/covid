import React from "react";
import { mount } from "enzyme";
import App from "./App";

function render(args) {
  const defaultProps = {
    match: {},
  };

  const props = { ...defaultProps, ...args };

  return mount(<App {...props} />);
}

it("test to pass", () => {});
