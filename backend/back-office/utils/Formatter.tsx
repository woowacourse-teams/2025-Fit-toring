export const formatPrice = (price: number) => new Intl.NumberFormat("ko-KR").format(price) + "원";
export const formatDateTime = (dateString: string) =>
    new Date(dateString).toLocaleString("ko-KR", {
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
});
