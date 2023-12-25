from loguru import logger

class BaseTestCase:

    def __init__(self, schema, port):
        self.schema = schema
        self.port = port

    def start(self):
        logger.info (f"--------START {self.schema}------")

    def stop(self):
        logger.info(f"--------STOP {self.schema}------")

    def on_error(self):
        logger.info(f"--------ERROR {self.schema}------")
