export interface ProductLocation {
    x: number;
    y: number;
}

export interface Product {
    id: number;
    name: string;
    quantity: number;
    price: number;
    location: ProductLocation;
}

export type OrderItem = {
    productId: number;
    quantity: number;
}

export interface MissingItems {
    productName: string;
    requested: number
    available: number;
}

export interface OrderSuccessResponse {
    status: "SUCCESS";
    orderId: number;
    message: string
}

export interface OrderFailureResponse {
    status: "FAIL";
    message: string;
    missingItems: MissingItems[];
}

export type OrderResponse = OrderSuccessResponse | OrderFailureResponse;

export type Location = [number, number];

export interface BotRoute {
    routeName: string;
    visitedLocations: Location[];
}