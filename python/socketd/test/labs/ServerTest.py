import asyncio

from socketd import SocketD

async def main():
    await SocketD.create_server("sd:ws") \
        .config(lambda c: c.port(8602).fragment_size(1024 * 1024)) \
        .start()


if __name__ == "__main__":
    asyncio.run(main())