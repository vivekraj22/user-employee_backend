import { afterAll, afterEach, beforeAll} from 'vitest'
import { setupServer } from 'msw/node'
import { rest } from 'msw'
import "whatwg-fetch"
import {users} from '../tests/mockUser'

export const restHandlers = [
    rest.post('/api/users', async (req, res, ctx) => {
        console.log("in mock test");
        let inserted = true;
        let newUser = req.json();
        users.push(await newUser);
        console.log("New user added: ", newUser);
        if(inserted) {
            return res(ctx.status(201), ctx.json({message: "user added successfully." }));
        }else{
            return res(ctx.status(404), ctx.json({message: "User already exist"}));
        }
    }),
]

const server = setupServer(...restHandlers)

// Start server before all tests

beforeAll(() => server.listen({ onUnhandledRequest: 'error' }))

//  Close server after all tests

afterAll(() => server.close())

// Reset handlers after each test `important for test isolation`

afterEach(() => server.resetHandlers())