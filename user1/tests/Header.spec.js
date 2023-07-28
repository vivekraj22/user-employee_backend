import { describe, test, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/vue";
import Header from "../src/components/Header.vue"

describe("header",async () => {
    
    test(" check header component", () => {
        expect(Header).toBeTruthy();
    }) 

    test("img_address prop is required", () => {
        expect(Header.props.img_address.required).toBe(true);
      });
      

    test("company prop is required", () => {
        expect(Header.props.company.required).toBe(true);
      });
      

    test("renders the component with the correct image and company name", () => {
        const imgAddress = "https://media.licdn.com/dms/image&v=beta&t";
        const companyName = "abc";
    
        render(Header, {
          props: {
            img_address: imgAddress,
            company: companyName,
          },
        });
    
        const imgElement = screen.getByAltText("User Database");
        const companyNameElement = screen.getByText(
          `Welcome to ${companyName}'s User Database`
        );
    
        expect(imgElement).toBeDefined();
        expect(imgElement.src).toBe(imgAddress);
        expect(companyNameElement).toBeDefined();
      });
    
})

