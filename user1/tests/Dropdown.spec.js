import { describe, test, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/vue";
import Dropdown from "../src/components/Dropdown.vue";

describe("dropdown", async () => {
  test(" check dropdown component", () => {
    expect(Dropdown).toBeTruthy();
  });

  test("renders dropdown options correctly", async () => {
    const options = [
      { name: "1", code: "Option 1" },
      { name: "2", code: "Option 2" },
      { name: "3", code: "Option 3" },
    ];

    render(Dropdown, {
      global: {
        mocks: {
          ["$primevue"]: {
            config: {
              locale: {
                emptySelectionMessage: "Your empty selection message",
              },
            },
          },
        },
      },
      props: {
        tasks: options,
        taskName: "Select an option",
      },
    });

    // Check if placeholder is displayed
    const label1 = await screen.findByText("Select an option");
    expect(label1.innerHTML).toContain("Select an option");

    // Check if dropdown options are rendered correctly
    const dropdownButton = await screen.findByRole("combobox");
    fireEvent.click(dropdownButton);

    const dropdownOptions = await screen.findAllByRole("option");
    expect(dropdownOptions.length).toBe(options.length);
    // screen.debug(dropdownOptions)
  });
});
