import { describe, test, expect } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/vue";
import Userform from "../src/components/Userform.vue";
import Dropdown from "../src/components/Dropdown.vue";
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Calendar from "primevue/calendar"; 

describe("userform detailed test ", () => {

    render(Userform, {
        global: {
          components: {
            InputText,
            Button,
            Dropdown,
            Calendar
          },
          mocks: {
            $primevue: {
              config: {
                locale: {
                  emptySelectionMessage: "Your empty selection message",
                },
              },
            },
          },
        },
        props: {
          propname: "get",
          propJson: {},
        },
      });

  test("create user test", async () => {
    const tasks = [
      { name: "Male", code: "male" },
      { name: "Female", code: "female" },
    ];
    const statuses = [
        { name: "Active", code: "active" },
        { name: "Inactive", code: "inactive" },
      ];

    const label1 = await screen.findByLabelText('Username');
    expect(label1.innerHTML).toBe('');
    await fireEvent.update(label1, 'ram');
    expect(label1.value).toContain('ram');

    const label2 = await screen.findByLabelText('Email');
    expect(label2.innerHTML).toBe('');
    await fireEvent.update(label2, 'ram@mail.com');
    expect(label2.value).toContain('ram@mail.com');

    const comboboxes = screen.getAllByRole('combobox');
    expect(comboboxes).toHaveLength(3);

    const dropdown1 = await screen.findByRole('combobox', { name: 'Gender' });
    expect(dropdown1).toBeTruthy();
    fireEvent.click(dropdown1);
    const genderOption = await screen.findByRole("option", { name: "Male" });
    await fireEvent.click(genderOption);

    const dropdown2 = await screen.findByRole('combobox', { name: 'Status' });
    expect(dropdown2).toBeTruthy();
    fireEvent.click(dropdown2);
    const statusOption = await screen.findByRole("option", { name: "Active" });
    await fireEvent.click(statusOption);

    const calendarInput = screen.getByPlaceholderText("Timestamp");
    expect(calendarInput).toBeTruthy('');
    await fireEvent.update(calendarInput, "23-09-2023");
    expect(calendarInput.value).toContain("23-09-2023");

    const submitButton = await screen.findByText('Submit');
    await fireEvent.click(submitButton);
  });

  test("get user test", async () => {

  })


});
